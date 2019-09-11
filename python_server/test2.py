import pymysql.cursors
 
conn = pymysql.connect(host='18.191.10.193',
        user='kimcheon',
        password='kim2cheon1',
        db='SHOWOOMI',
        charset='utf8')
 
try:
    with conn.cursor() as cursor:
        sql = 'INSERT INTO Product(productId, optionNum, name, category, length, image, price, size, color, fabric, pattern, detail) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
        cursor.execute(sql, ('hi','hi','hi','hi','hi','hi','hi','hi','hi','hi','hi','hi'))
    conn.commit()
    print(cursor.lastrowid)
    # 1 (last insert id)
finally:
    conn.close()

    
