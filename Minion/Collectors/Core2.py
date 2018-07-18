import paramiko
import time
import subprocess

def getData(host, n):
    actv = 0
    idle = 0
    for i in range(-1,2,2):
        cmd = 'cat /proc/stat | grep cpu0'
        ps = subprocess.Popen(cmd,shell=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
        output = ps.communicate()[0]
        vals = str(output)[7:-3].split(' ')
        idle = idle + (i*int(vals[3]))
        for q in range(0,9):
            actv = actv + (i*int(vals[q]))
        actv = actv - (i*int(vals[3]))
        time.sleep(1)
    util = (100*float(actv))/float((actv+idle))
    return util
