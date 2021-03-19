#!/usr/bin/env python
# -*- coding:utf-8 -*-
# Author: Naccl

import requests
from bs4 import BeautifulSoup
import os
import pymysql
import datetime
import sys

db = None
cursor = None
# articleID = 287120

def requests_get(url):
    try:
        response = requests.get(url, timeout=(3, 3))
        if response.status_code == 200:
            return response
    except Exception as e:
        print(e)
        for i in range(1,4):
            print("请求超时,第%s次重复请求,url=%s" % (i, url))
            response = requests.get(url, timeout=(3, 3))
            if response.status_code == 200:
                return response
    return -1


def executeSQL(field):
    global db
    global cursor
    sql = '''INSERT INTO article(id, title, author, date, description, first_picture, content, star, views)
        VALUES ("%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s")''' % (field[0], field[1], field[2], field[3], field[4], field[5], field[6], 0, 0)
    try:
        cursor.execute(sql)
        db.commit()
        result = cursor.fetchall()
        print(result)
    except Exception as e:
        print(e)
        db.rollback()


def getContent(articleID):
    url = "http://www.chuapp.com/article/%s.html" % articleID
    try:
        r = requests_get(url)
        bs = BeautifulSoup(r.text, "lxml")
        page = bs.select("body > div.content.single > div > div.the-content")  # 获取正文内容

        p = list(page[0])
        while "\n" in p:  # 去除空行
            p.remove("\n")

        content = ""
        for i in range(len(p)):
            imgTag = p[i].find("img")
            if imgTag != None:
                imgUrl = imgTag["src"].split("?")[0]
                content += "<img src='%s'>" % imgUrl
            else:
                if p[i].text == "\n":  # 文本是换行符
                    content += "<%s></%s>" % (p[i].name, p[i].name)
                else:
                    content += "<%s>%s</%s>" % (p[i].name, p[i].text, p[i].name)
        return content
    except Exception as e:
        print(e)
        return None


def getRecommend(pageNumber, num):  # pageNumber：每日聚焦id，num：每日聚焦页面中文章序号
    url = "http://www.chuapp.com/category/index/id/daily/p/%s.html" % pageNumber
    try:
        r = requests_get(url)
        bs = BeautifulSoup(r.text, "lxml")

        aTagSelect = "body > div.content.category.fn-clear > div.category-left.fn-left > div > a:nth-child(%s)" % num
        titleSelect = "body > div.content.category.fn-clear > div.category-left.fn-left > div > a:nth-child(%s) > dl > dt" % num
        authorSelect = "body > div.content.category.fn-clear > div.category-left.fn-left > div > a:nth-child(%s) > dl > dd.fn-clear > span.fn-left > em" % num
        dateSelect = "body > div.content.category.fn-clear > div.category-left.fn-left > div > a:nth-child(%s) > dl > dd.fn-clear > span.fn-left" % num
        descriptionSelect = "body > div.content.category.fn-clear > div.category-left.fn-left > div > a:nth-child(%s) > dl > dd:nth-child(3)" % num

        aTag = bs.select(aTagSelect)
        title = bs.select(titleSelect)
        author = bs.select(authorSelect)
        date = bs.select(dateSelect)
        description = bs.select(descriptionSelect)

        articleID = aTag[0]["href"].split(".")[0][-6:]  # 从aTag中获取文章id
        firstPictureUrl = aTag[0].find("img")["src"].split("?")[0]  # 从aTag中获取firstPictureUrl
        dateStr = date[0].text[len(author[0].text):]
        if "前" in dateStr:
            today = datetime.date.today()
            dateStr = "%02d月%02d日" % (today.month, today.day)

        field = [articleID, title[0].text, author[0].text, dateStr, description[0].text, firstPictureUrl]
        return field
    except Exception as e:
        print(e)
        return None


def start(page):
    global db
    global cursor
    db = pymysql.connect("localhost", "root", "root", "gamebox")
    cursor = db.cursor()
    getTop(page)
    db.close()


def getTop(page):
    for i in range(1, page + 1):
        pageNumber = i
        for j in range(1, 11):
            field = getRecommend(pageNumber, j)
            if field != None:
                content = getContent(field[0])
                if content != None:
                    field.append(content)
                    executeSQL(field)


if __name__ == "__main__":
    args = sys.argv
    if len(args) == 2:
        if int(args[1]) > 0:
            start(int(args[1]))
        else:
            print("args error")
    else:
        print("input a number of page")
