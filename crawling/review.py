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

chrome_driver_path = 'C:/Users/user/Downloads/chromedriver_win32/chromedriver.exe'
driver = webdriver.Chrome(chrome_driver_path,  chrome_options=options)
driver.implicitly_wait(3)

#상품 id 목록
product_id = ['1125616073']

for x in product_id:
    # url 설정
    url_r = 'http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo='
    url_id = x
    url = url_r + url_id

    # url 접근
    driver.get(url)
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    for e in soup.find_all("div", {"class": "deal_rank_ct"}):
        #평점
        rank = e.find('strong').text
        print("구매만족도 평점 : ",rank)
        for i in e.find_all("div", {"class": "ct"}):
            #평가항목
            ct = i.find('h5').text
            print(ct)
            for j in i.find_all("li"):
                #세부항목
                stit = j.find("span", {"class": "stit"}).text
                score = j.find("span", {"class": "num"}).text
                print(stit,"(",score,")")

    # 리뷰부분 시작 - 해당 iframe 접근 => 아직 수정 중.
    editor_frame = driver.find_element_by_id("ifrmReview")
    driver.switch_to_frame(editor_frame)

    #최신순으로 변경
    driver.find_element_by_xpath('//*[@id="sortCd"]/option[2]').click()

    html = driver.page_source

    e = soup.find("div", {"class": "ifrm_prdc_review"})
    i = e.find("div", {"class": "review_list"})
    for j in i.find_all("li"):
                option = j.find("p", {"class": "option_txt"}).text
                review = j.find("a", {"id": "reviewContTxt"}).text
                print(option)
                print(review)

    
            
    


            
