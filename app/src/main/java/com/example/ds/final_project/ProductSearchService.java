package com.example.ds.final_project;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2019-03-19.
 */

public class ProductSearchService {
    //요청을 하기 위한 ID와 SECRET
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    private static final int PAGE_SIZE=20; //출력되는 상품수
    private int PAGE_NUM=1; //페이지번호
    private String sortCd="CP"; //정렬순 //CP:인기순 A:누적판매순 G:평가높은순 I:후기/리뷰많은순 L:낮은가격순 H:높은가격순 N:최근등록순

    // 상품검색 요청 URL
    String URL="https://openapi.11st.co.kr/openapi/OpenApiService.tmall?key=ad722ec66e955e9c584c2b828158dee9&apiCode=ProductSearch&pageNum="+PAGE_NUM+"&pageSize="+PAGE_SIZE+"&sortCd="+sortCd+"&keyword=";
    //상품정보조회 요청 URL
    String URL_DETAIL="http://openapi.11st.co.kr/openapi/OpenApiService.tmall?key=ad722ec66e955e9c584c2b828158dee9&apiCode=ProductInfo&productCode=";


    //현재 페이지를 알기 위한 상태값
    private int currentSkip = 1;

    // 검색할 키워드
    private String keyword;
    //상품코드
    private String productcode;




    public ProductSearchService(String keyword){
        PAGE_NUM=1;
        this.keyword = keyword;
        URL = URL + keyword;
//            nextPage();
//            Log.i("페이지넘버",""+getPAGE_NUM());
//            URL = "https://openapi.11st.co.kr/openapi/OpenApiService.tmall?key=ad722ec66e955e9c584c2b828158dee9&apiCode=ProductSearch&pageNum="+PAGE_NUM+"&pageSize="+PAGE_SIZE+"&keyword=";
//            this.keyword=keyword;
//            URL = URL + keyword;

    }

    public void nextPage(String keyword) {
        PAGE_NUM += 1;
        URL = "https://openapi.11st.co.kr/openapi/OpenApiService.tmall?key=ad722ec66e955e9c584c2b828158dee9&apiCode=ProductSearch&pageNum="+PAGE_NUM+"&pageSize="+PAGE_SIZE+"&sortCd="+sortCd+"&keyword=";
        this.keyword=keyword;
        URL = URL + keyword;
    }


    public int getPAGE_NUM(){
        return PAGE_NUM;
        //nextPage함수가 한번이상 불려서 currentSkip이 1이 아니면
        //처음 검색이 아니고 아이템 추가
    }
    //상품검색 조회
    public List<Product> search() {
        List<Product> list = null;
        try {
            URL url;
            url = new URL(URL+ URLEncoder.encode(keyword, "EUC-KR"));
            URLConnection urlConn = url.openConnection();

            // xml파서객체만들고
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            // 요청에 대한 응답 결과를 파서에 세팅
            parser.setInput(new InputStreamReader(urlConn.getInputStream(),"EUC-KR"));
            int eventType = parser.getEventType();

            Product p = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.END_DOCUMENT: // 문서의 끝
                        break;
                    case XmlPullParser.START_DOCUMENT:
                        list = new ArrayList<Product>();
                        break;
                    case XmlPullParser.END_TAG: {
                        String tag = parser.getName();
                        if (tag.equals("Product")) {
                            list.add(p);
                            p = null;
                        }
                    }
                    case XmlPullParser.START_TAG: {
                        String tag = parser.getName();
                        switch (tag) {
                            case "Product":
                                p = new Product();
                                break;
                            case "ProductCode":
                                if (p != null){
                                    p.setProductCode(parser.nextText());
                                }
                                break;
                            case "ProductName":
                                if (p != null){
                                    p.setProductName(parser.nextText());}
                                break;
                            case "ProductImage300":
                                if (p != null){
                                    p.setProductImage(parser.nextText());
                                }
                                break;
                            case "ProductPrice":
                                if (p != null){
                                    p.setProductPrice(parser.nextText());
                                }
                                break;
                            case "DetailPageUrl":
                                if (p != null){
                                    p.setProductDetailUrl(parser.nextText());}
                                break;
                        }
                    }
                }
                eventType = parser.next();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    //상세정보 조회
    public List<Product> search_detail(List<Product> productList, String color) {
        for (int i = 0; i < productList.size(); i++) {
            URL_DETAIL="http://openapi.11st.co.kr/openapi/OpenApiService.tmall?key=ad722ec66e955e9c584c2b828158dee9&apiCode=ProductInfo&productCode=";
            Product p=productList.get(i);
            productcode=p.getProductCode();
            URL_DETAIL=URL_DETAIL+productcode+"&option=PdOption";

            try {
                java.net.URL url;
                url = new URL(URL_DETAIL);
                URLConnection urlConn = url.openConnection();

                // xml파서객체만들고
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                // 요청에 대한 응답 결과를 파서에 세팅
                parser.setInput(new InputStreamReader(urlConn.getInputStream(), "EUC-KR"));
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.END_DOCUMENT: // 문서의 끝
                            break;
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.END_TAG: {
                            break;
                        }
                        case XmlPullParser.START_TAG: {
                            String tag = parser.getName();
                            switch (tag) {
                                case "Order":
                                    if (p != null) {
                                        String a=parser.nextText();
                                        p.setOptionOrder(a);
                                    }
                                    break;
                                case "TitleName":
                                    if (p != null) {
                                        String title=parser.nextText();
                                        p.setOptionTitle(title);
                                    }
                                    break;
                                case "ValueName":
                                    if (p != null) {
                                        String name=parser.nextText();
                                        if(name.contains(color)){ //색 필터링
                                            p.setOptionValueList(name);
                                            p.setChagneValue(1);//추가됐을때
                                        }
                                        else
                                            p.setChagneValue(0); //추가안됐을때
                                    }
                                    break;
                                case "Price":
                                    if (p != null&&p.getOptionValueList().size()>0&&p.getChangeValue()==1) {
                                        String price=parser.nextText();
                                        p.setOptionPriceList(price);
                                    }
                                    break;
                            }
                        }
                    }
                    eventType = parser.next();
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return productList;
    }


}

