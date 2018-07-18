import os.path
import os
import time
import re

#from Util import Sleep

#Makes path (if passed) os independent
def convertPath(path):
    separator = os.path.sep

    if separator == '/':
        path = path.replace('\\',os.path.sep)

    elif separator =='\\':
        path = path.replace('/',os.path.sep)

    return path

def ParseFile(Filename,Pattern,Tokens,Index):
    Filename = convertPath(Filename)
    # This could probably be cleaner
    while True:
        try:
            file = open(Filename,'rt')
            break
        except Exception:
            print('No file yet')
            time.sleep(1)

    try:
        lines = file.readlines()
    except Exception as Ex:
        return "File [" + Filename + "] " + str(ex)

    file.close()
    # Build  List (Framesize, pps, mbps)
    data = []
    for line in lines[1:]:
        x=line.split(',')
        data.append((int(x[0]), int(x[1]), int(x[2])))
    # End build list
    if Pattern == 'Null':
        return data
    else:
        return __ParseFile(lines,Pattern,Tokens,Index)

def __ParseFile(lines,Pattern,Tokens,Index):
    Index = int(Index) #ensure it's an int and not a string
    try:
        for line in lines:
            if Pattern in line: #look for the desired pattern
                strRet = line.split(Tokens) #use the tokens to break line up into an array of separate strings
                return strRet[Index].lstrip().rstrip()

    except Exception as ex:
        pass

    return "Error parsing file in File Collector"

