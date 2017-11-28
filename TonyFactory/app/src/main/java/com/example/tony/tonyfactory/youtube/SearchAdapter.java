package com.example.tony.tonyfactory.youtube;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.tony.tonyfactory.utils.Dispatch;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-11-28.
 */

public class SearchAdapter extends ArrayAdapter<String> implements Filterable {

    private Filter filter = new KNoFilter();
    public List<String> items;

    public SearchAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        items = objects;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null) {
                return results;
            }
            String query = constraint.toString();
            if (query == null) {
                return results;
            }
            try {
                query = URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String url = "http://google.com/complete/search?output=toolbar&q="+query+"&ie=utf8&oe=utf8";
            new Dispatch<List<String>>() {

                @Override
                public List<String> onWork() throws IOException {
                    List<String> result = new ArrayList<String>();
                    try {
                        InputStream download = new URL(url).openStream();
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(download, "utf-8");
                        int eventType = xpp.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {
                                if (xpp.getName().equals("suggestion")) {
                                    String suggestion = xpp.getAttributeValue(null, "data");
                                    result.add(suggestion);
                                }
                            }
                            eventType = xpp.next();
                        }
                        download.close();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return result;
                }

                @Override
                public void onComplete(List<String> result) throws IOException {
                    items.clear();
                    items.addAll(result);
                    notifyDataSetChanged();
                }
            };
            results.values = items;
            results.count = items.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }
}
