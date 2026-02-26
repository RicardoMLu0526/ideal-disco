# 执行数据库初始化脚本
mysql -u root -proot -e "source F:\Program\service\order-service\src\main\resources\init.sql"

# 执行测试数据脚本
mysql -u root -proot -e "source F:\Program\service\order-service\src\test\resources\test-data.sql"

Write-Host "数据库初始化和测试数据插入完成"