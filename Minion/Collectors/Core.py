import paramiko
import time

def getData(host, n):
    actv = 0
    idle = 0
    for i in range(-1,2,2):
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(host, username='root', password='netronome')
        session = ssh.get_transport().open_session()
        cmd = 'cat /proc/stat | grep cpu' + str(n)
        session.exec_command(cmd)
        stdout = session.makefile('r',-1)
        stdout_text = stdout.read()
        ssh.close()
        if int(n) < 10: vals = str(stdout_text)[7:-3].split(' ')
        else: vals = str(stdout_text)[8:-3].split(' ')
        idle = idle + (i*int(vals[3]))
        for q in range(0,9):
            actv = actv + (i*int(vals[q]))
        actv = actv - (i*int(vals[3]))
        if i==-1: time.sleep(0.5)
    util = (100*float(actv))/float((actv+idle))
    return util
