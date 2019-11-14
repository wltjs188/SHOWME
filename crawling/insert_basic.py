from bs4 import BeautifulSoup
from selenium import webdriver
import re
import json
import ast
import pymysql.cursors

# 드라이버 설정
options = webdriver.ChromeOptions()
options.add_argument('headless')
options.add_argument('window-size=1920x1080')
options.add_argument("disable-gpu")

chrome_driver_path = 'C:/Users/SAMSUNG/Downloads/chromedriver_win32/chromedriver.exe'
driver = webdriver.Chrome(chrome_driver_path,  chrome_options=options)
driver.implicitly_wait(3)

#상품 id 목록
product_id = ['2561192508','1125616073','2118337055','106598793']

for x in product_id:
    # url 설정
    url_r = 'http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo='
    url_id = x
    url = url_r + url_id

    # url 접근
    driver.get(url)
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    for e in soup.find_all('li', {'class' : 'photo'}):
        print("상품id",url_id)
        a = e.find('a')
        if 'data-optvalueno' in a.attrs:
            optionNum = a.attrs['data-optvalueno']
            print("옵션번호",optionNum)
        if 'data-dtloptnm' in a.attrs:
            name = a.attrs['data-dtloptnm']
            print("상품명",name)
        for b in e.find_all('img'):
            if 'src' in b.attrs:
                img = b.attrs['src']
                print("대표이미지",img)
        for c in e.select('em'):
            price = c.text
            print("가격",price)
        conn = pymysql.connect(host='18.191.10.193',
        user='kimcheon',
        password='kim2cheon1',
        db='SHOWOOMI',
        charset='utf8')
        try:
            with conn.cursor() as cursor:
                sql = 'INSERT INTO Product(productId, optionNum, name, image, price) VALUES (%s,%s,%s,%s,%s)'
                cursor.execute(sql, (url_id,optionNum,name,img,price))
            conn.commit()
            print(cursor.lastrowid)
            # 1 (last insert id)
        finally:
            conn.close()


            
