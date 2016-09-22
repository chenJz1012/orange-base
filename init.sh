#!/usr/bin/env bash
mysql -uroot -proot1234 -e "drop database if exists orange_base;"
mysql -uroot -proot1234 -e "create database orange_base DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;"
redis-cli -a 123456 keys "*" | grep orange_ | xargs redis-cli -a 123456 del
