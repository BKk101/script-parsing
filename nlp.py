#-*- coding:utf-8 -*-
from konlpy.tag import Hannanum
import re

hannanum = Hannanum()
filepath = './in.txt'

p = re.compile('\S')
f = open(filepath, 'r', encoding="UTF-8")
f2 = open('./out.txt', 'w', encoding="UTF-8")
while True:
    line = f.readline()
    if not line: break
    if p.search(line):
        morph = hannanum.nouns(line)
        # print(hannanum.pos(line))
        f2.writelines(' '.join(morph))
        f2.write('\n')

f.close()
f2.close()
