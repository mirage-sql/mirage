SELECT BOOK_ID, BOOK_NAME, AUTHOR, PRICE
FROM BOOK
/*IF bookName != null*/
WHERE BOOK_NAME IN /*bookNames*/('Mirage in Action')
/*END*/
