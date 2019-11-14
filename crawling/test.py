from bs4 import BeautifulSoup
from selenium import webdriver
import re
import json
import ast
import pymysql.cursors

# 드라이버 설정
options = webdriver.ChromeOptions()

chrome_driver_path = 'C:/Users/user/Downloads/chromedriver_win32/chromedriver.exe'
driver = webdriver.Chrome(chrome_driver_path,  chrome_options=options)
driver.implicitly_wait(3)

#상품 id 목록
product_id = ['1017008148']

for x in product_id:
    # url 설정
    url_r = 'http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo='
    url_id = x
    url = url_r + url_id

    # url 접근
    driver.get(url)
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    # 리뷰부분 시작 - 해당 iframe 접근 => 아직 수정 중.
    editor_frame = driver.find_element_by_id("ifrmReview")
    driver.switch_to_frame(editor_frame)
    
    #옵션으로 검색
    driver.find_element_by_name('detailViewSearchBtn').send_keys('HC8044')
    driver.find_element_by_xpath('/html/body/div/div[1]/div/div[1]/fieldset/button').click()
    #최신순으로 변경
    driver.find_element_by_xpath('//*[@id="sortCd"]/option[2]').click()

    soup = BeautifulSoup(driver.page_source, 'html.parser')

    e = soup.find("div", {"class": "ifrm_prdc_review"})
    print(e)
    i = e.find("div", {"class": "review_list"})
    for j in i.find_all("li"):
                option = j.find("p", {"class": "option_txt"}).text
                review = j.find("span", {"class": "summ_txt"})
                print(option)
                print(review)

    
            
    


            
