@echo off

REM 执行数据库初始化脚本
mysql -u root -proot < "%~dp0src\main\resources\init.sql"

REM 执行测试数据脚本
mysql -u root -proot < "%~dp0src\test\resources\test-data.sql"

echo 数据库初始化和测试数据插入完成
