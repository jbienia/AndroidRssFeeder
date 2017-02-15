package com.jason.rssprocessing;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

   private  String FEED_URL="https://www.1843magazine.com/rss/content";
    private URL feedURL;
    private BufferedReader bufferedReader;
    private boolean inTitle,isLink,isItem,isDescription,inDate;
    public String description;
    public AdapterRss arrayAdapter;
    public ArrayList<DisplayItems> title;
    public DisplayItems displayItems;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView)findViewById(R.id.lvRss);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,MoreInformation.class);
                intent.putExtra("description",title.get(i).getDescription());
                intent.putExtra("url",title.get(i).getLink());
                startActivity(intent);
            }
        });
   }

    class FreepHandler extends DefaultHandler{

        StringBuilder stringBuilder;


        public FreepHandler(){
            title = new ArrayList<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if(qName == "item")
            {
                isItem = true;
            }

            if(qName.equals("title") && isItem){

                displayItems = new DisplayItems();
                stringBuilder = new StringBuilder(50);
                inTitle = true;
                isLink = true;
            }

            if(qName.equals("pubDate") && isItem)
            {
                stringBuilder = new StringBuilder(50);
                inDate  = true;
            }

            if(qName.equals("description") && isItem)
            {
                stringBuilder = new StringBuilder(50);
                isDescription = true;
            }

            if(qName.equals("link")&& isItem)
            {
                stringBuilder = new StringBuilder(50);
                isLink = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            inTitle = false;
            inDate  = false;
            isDescription = false;
            isLink = false;

            if(qName == "pubDate" && isItem)
            {
                title.add(displayItems);
                //Log.d("JB", stringBuilder.toString());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            if(inTitle)
            {
                stringBuilder.append(ch,start,length);
                displayItems.setTitle(stringBuilder.toString());
            }

            if(inDate)
            {
                stringBuilder.append(ch,start,length);
                displayItems.setDate(stringBuilder.toString());
            }

            if(isDescription)
            {
                displayItems.setDescription( stringBuilder.append(ch,start,length).toString());
            }

            if(isLink)
            {
                displayItems.setLink(stringBuilder.append(ch,start,length).toString());
            }
        }
    }

    class AsyncTest extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Object doInBackground(Object[] params) {

            try {
                //create a SAX parser
                SAXParserFactory spf = SAXParserFactory.newInstance();

                //create URL object
                feedURL = new URL(FEED_URL);

                //create SAXParser
                SAXParser saxParser = spf.newSAXParser();

                //create instance of FreepHander
                FreepHandler freepHandler = new FreepHandler();

                //open the stream from the URL
                bufferedReader = new BufferedReader(
                        new InputStreamReader(feedURL.openStream()));

                //parse the stream of data using our custom handler
                saxParser.parse(new InputSource(bufferedReader), freepHandler);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            arrayAdapter = new AdapterRss(MainActivity.this,R.layout.feeder,title);
            listView.setAdapter(arrayAdapter);
        }
    }

    public void loadAeonRssFeed()
    {
        FEED_URL = "http://www.winnipegfreepress.com/rss/?path=%2Fbreakingnews";
        AsyncTest asyncTest = new AsyncTest();
        asyncTest.execute();
    }

    public void loadNautilusRssFeed()
    {
        FEED_URL = "https://www.1843magazine.com/rss/content";
        AsyncTest asyncTest = new AsyncTest();
        asyncTest.execute();
    }

    private class AdapterRss extends ArrayAdapter<DisplayItems> {

        private ArrayList<DisplayItems> items;

        public AdapterRss(Context context, int textViewResourceId, ArrayList<DisplayItems> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.feeder, null);
            }
            DisplayItems o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText( o.getTitle());
                   // Log.d("JBBBBBB", o.getTitle());
                }
                if (bt != null) {
                    bt.setText( o.getDate());
                }
            }
            return v;
        }
    }

    class DisplayItems
    {
        private String title;
        private String date;
        private String description;
        private String link;

        public DisplayItems()
        {

        }

        public String getTitle()
        {
            return this.title;
        }
        public String getLink()
        {
            return this.link;
        }
        public String getDescription()
        {
            return this.description;
        }
        public String getDate(){return this.date;}

        public void setTitle (String title)
        {
         this.title = title;
        }

        public void setLink(String link)
        {
            this.link = link;
        }
        public void setDescription(String description){this.description = description;}
        public void setDate(String date){this.date = date;}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_selection, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean loadFeed(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.AeonFeed:
            loadAeonRssFeed();
                return  true;

            case R.id.NautilusFeed:
            loadNautilusRssFeed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

