
import pymysql.cursors




            
conn = pymysql.connect(host='52.78.143.125',
        user='kimcheon',
        password='kim2cheon1',
        db='SHOWME',
        charset='utf8')
 
curs = conn.cursor()
sql = 'INSERT INTO SIZE_SKIRT(ID, SIZE, TOTAL, WAIST, TAIL) VALUES (%s,%s,%s,%s,%s)'
curs.execute(sql, (124,'XL,히',1,1,1))

conn.commit()
 
conn.close()
