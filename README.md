Database Management Systems Project

MARSYS – Supermarket Automation System
The program we created is a Java MVC-based ERP-like supermarket automation system. Our program uses a cloud-based PostgreSql database. This program contains the features below;
Sale, Return, Reports, Stock and Inventory, Employee Management, Discount and Campaign Management.
We preferred a cloud-based PostgreSql database so we can take control on database everywhere. We didn’t use our DB connection string hard-coded to avoid anyone access the DB without permission.

Database Details:
This program’s database has 7 tables: CAMPAIGN, CARDS, COUPON, EMPLOYEE, INVENTORY, INVOICES, STOCK_MOVEMENT
Columns of the tables:
CAMPAIGN: CAMPAIGN_ID, DISCOUNT_TYPE, DISCOUNT_TYPE_CODE, DISCOUNT_FOR, START_DATE, END_DATE, IS_ACTIVE
CARDS: FIRST_NAME, LAST_NAME, NUMBER, PASSWORD, BALANCE
COUPON: COUPON_CODE, DISCOUNT_AMOUNT, START_DATE, END_DATE, IS_ACTIVE, USED, USING_LIMIT
EMPLOYEE: ID, NAME, LAST_NAME, POSITION, PASSWORD, STORE_CODE, START_DATE, END_DATE, BIRTH_DATE, COUPON_CODE
INVENTORY: BARCODE, NAME, QUANTITY, SALE_PRICE, CATEGORY, BRAND, BUYING_PRICE, EXPIRATION
INVOICES: INVOICE_NUMBER, PAYMENT_TYPE, CARD_NUMBER, PAID_AMOUNT, DISCOUNT_AMOUNT, ACTUAL_CART_AMOUNT, CASHIER_ID, DATE, ORIGINAL_INVOICE_NUMBER
STOCK_MOVEMENT: MOVEMENT_ID, MOVEMENT_TYPE, BARCODE, INVOICE_NUMBER, USER, DATE
