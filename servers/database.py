import MySQLdb

def executer(sql):
	try:
		db=MySQLdb.connect("localhost","root","password","varnan")
	except:
		print "connection failed"
		return
	cursor=db.cursor()
	cursor.execute(sql)
	db.commit()
	db.close()
	return

def counter():
	try:
		db=MySQLdb.connect("localhost","root","password","varnan")
	except:
		print "connection failed"
		return
	cursor=db.cursor()
	cursor.execute("SELECT * FROM REPORTS")
	db.commit()
	db.close()
	return int(cursor.rowcount)+1
	
def updater(fixer):
	try:
		db=MySQLdb.connect("localhost","root","password","varnan")
	except:
		print "connection failed"
		return
	cursor=db.cursor()
	cursor.execute("SELECT * FROM REPORTS WHERE FORWARDEDTO='%s' AND STATUS='WORKING'"%(fixer))
	if(cursor.rowcount>0):
		result=cursor.fetchone()
		ID=str(int(result[0]))
		DESCRIPTION=result[1]
		GPS=result[2]
		return(','.join((ID,DESCRIPTION,GPS)))
		
	cursor.execute("SELECT * FROM REPORTS WHERE FORWARDEDTO='ORGANISATION'")
	if(cursor.rowcount==0):
		ID='NONE'
		DESCRIPTION='NO MORE COMPLAINTS LEFT'
		GPS='0.0,0.0'
		return(','.join((ID,DESCRIPTION,GPS)))
	result=cursor.fetchone()
	ID=str(int(result[0]))
	DESCRIPTION=result[1]
	GPS=result[2]
	sql="UPDATE REPORTS SET STATUS='WORKING',FORWARDEDTO='%s' WHERE ID='%s'"%(fixer,ID)
	cursor.execute(sql)	
	db.commit()
	db.close()
	return(','.join((ID,DESCRIPTION,GPS)))
