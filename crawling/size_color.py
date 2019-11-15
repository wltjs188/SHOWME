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
product_id = ['2561192508','1125616073','2118337055']

for x in product_id:
    # url 설정
    url_r = 'http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo='
    url_id = x
    url = url_r + url_id

    # url 접근
    driver.get(url)
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    # 크롤링 시작 - 해당 iframe 접근
    editor_frame = driver.find_element_by_id("prdDescIfrm")
    driver.switch_to_frame(editor_frame)
    html = driver.page_source

    #정규표현식으로 리스트에 접근
    matched = re.search(r'var optList = (.*);', html)

    if not matched :
        continue
    optList = matched.group(0)
    cutstring = optList[20:-3]

    #json을 output_list에 삽입
    output_list = json.loads(cutstring)

    size = 'm'
    size_num = '28'
    size_big = 'XL'
    
    #json 차례대로 
    for output in output_list:
        second = 1
        third = 1
        
        optValueNo = output['optValueNo'] #옵션번호
        thirdOptValueNm = output['thirdOptValueNm'] #사이즈
        if not thirdOptValueNm :
            third = third - 1
        secondOptValueNm = output ['secondOptValueNm'] #색깔
        if not secondOptValueNm:
            second = second - 1
        retDtlExtNm = output['retDtlExtNm'] #상세이미지url

        if third == 1:
            print("둘다 1 => 사이즈/색상 따로",second,third)
            if size in secondOptValueNm:
                sizeValue = secondOptValueNm
                colorValue = thirdOptValueNm
            elif size_num in secondOptValueNm:
                sizeValue = secondOptValueNm
                colorValue = thirdOptValueNm
            elif size_big in secondOptValueNm:
                sizeValue = secondOptValueNm
                colorValue = thirdOptValueNm
            else:
                sizeValue = thirdOptValueNm
                colorValue = secondOptValueNm

            print("상품id",url_id)
            print("옵션번호",optValueNo)
            print("사이즈",sizeValue)
            print("색깔",colorValue)
            print("상세이미지",retDtlExtNm)
        
            conn = pymysql.connect(host='18.191.10.193',
            user='kimcheon',
            password='kim2cheon1',
            db='SHOWOOMI',
            charset='utf8')
            try:
                with conn.cursor() as cursor:
                    sql = "update Product set image_detail = %s, size = %s, color = %s where productId = %s and optionNum = %s"
                    cursor.execute(sql, (retDtlExtNm,sizeValue,colorValue,url_id,optValueNo))
                conn.commit()
                print(cursor.lastrowid)
                # 1 (last insert id)
            finally:
                conn.close()
            #print('{optValueNo} {retDtlExtNm}'.format(**output))
        elif third == 0:
            print("second만 1 => 사이즈/색상 동시에",second,third)
            print("상품id",url_id)
            print("옵션번호",optValueNo)
            print("사이즈/색깔",secondOptValueNm)
            print("상세이미지",retDtlExtNm) 
            conn = pymysql.connect(host='18.191.10.193',
            user='kimcheon',
            password='kim2cheon1',
            db='SHOWOOMI',
            charset='utf8')
            try:
                with conn.cursor() as cursor:
                    sql = "update Product set image_detail = %s, size_color = %s where productId = %s and optionNum = %s"
                    cursor.execute(sql, (retDtlExtNm,secondOptValueNm, url_id,optValueNo))
                conn.commit()
                print(cursor.lastrowid)
                # 1 (last insert id)
            finally:
                conn.close()
            #print('{optValueNo} {retDtlExtNm}'.format(**output))

    
        
            
 






