
import cv2
import numpy as np
import pytesseract
from  PIL import Image
import fileinput
from PIL import Image
from io import BytesIO 
import cv2
import requests

#tesseract 경로
tesseract_path="Tesseract-OCR"
pytesseract.pytesseract.tesseract_cmd=tesseract_path+'/tesseract'

#이미지 url
url = 'http://i.011st.com/ex_cmt/R/678x0/1/75/0/0/JPEG/src/http://gi.esmplus.com/monkeyjean/shocking/20190624/63_03.jpg?20190904162255'
response = requests.get(url)
img = Image.open(BytesIO(response.content))


width , height = img.size
print(width,height)

#이미지 아랫부분 자르기
area=(0,height-width*2,width,height)
cropped_img=img.crop(area)
cropped_img.save('test/crop.jpg')

img_matrix=np.array(cropped_img)


#Number='D:/test/table1.jpg' 
#img=cv2.imread(img_matrix,cv2.IMREAD_COLOR)
#copy_img=img.copy()

#전처리
gray=cv2.cvtColor(img_matrix,cv2.COLOR_BGR2GRAY)
cv2.imwrite('test/gray.jpg',gray)


blur = cv2.GaussianBlur(gray,(3,3),0)
cv2.imwrite('blur.jpg',blur)

ret, dst = cv2.threshold(gray, 200, 255, cv2.THRESH_BINARY)
cv2.imwrite('dst.jpg',dst)
#canny=cv2.Canny(blur,100,200)
#cv2.imwrite('D:/test/canny.jpg',canny)

#OCR
custom_oem_psm_config = 'user_words_suffix words'

#result=pytesseract.image_to_string(Image.open("D:/test/blur.jpg"),lang='kor+eng', config=custom_oem_psm_config)
result=pytesseract.image_to_string(dst,lang='kor+eng', config=custom_oem_psm_config)
#result=pytesseract.image_to_string(Image.open("D:/test/gray.jpg"),lang='kor+eng')
#result=pytesseract.image_to_string(Image.fromarray(gray))
print(result)


#분석한 텍스트 저장      
f = open('test/result.txt', mode='wt', encoding='utf-8')
f.write(result)
f.close()

#분석한 텍스트 배열에 저장 
a = [[0]*10 for i in range(10)]

row=0
col=0

f = open("test/result.txt", "r",encoding='UTF8' )
while True:
    line = f.readline()[:-1]
    if not line: break
    l=line.replace('|','')
    a[row]= l.split()
    col=len(a[row])
    row=row+1
    #print(line)
f.close()

r = [[0]*col for i in range(row)]


row=0
col=0
f = open("test/result.txt", "r",encoding='UTF8' )
while True:
    line = f.readline()[:-1]
    if not line: break
    l=line.replace('|','')
    r[row]= l.split()
    row=row+1

f.close()

#print(a)
#print("배열:"+result[0][0])
