import paramiko
import time

def getData(host, n):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(host, username='root', password='netronome')
    session = ssh.get_transport().open_session()
    cmd = 'cat /proc/stat | grep cpu' + str(n)
    session.exec_command(cmd)
    stdout = session.makefile('r',-1)
    stdout_text = stdout.read()
    ssh.close()
    values = str(stdout_text)[7:-3].split(' ')
    active = int(values[0])
    idle = int(values[3])
    for i in range(1,9):
        active = active + int(values[i])
    active = active - idle
    print(values)
    print(active)
    print(idle)
    time.sleep(1)
    # Round 2
    ssh.connect(host, username='root', password='netronome')
    session = ssh.get_transport().open_session()
    cmd = 'cat /proc/stat | grep cpu' + str(n)
    session.exec_command(cmd)
    stdout = session.makefile('r',-1)
    stdout_text = stdout.read()
    ssh.close()
    values2 = str(stdout_text)[7:-3].split(' ')
    active2 = int(values2[0])
    idle2 = int(values2[3])
    for i in range(1,9):
        active2 = active2 + int(values2[i])
    active2 = active2 - idle2
    print(values2)
    print(active2)
    print(idle2)
    active3 = active2-active
    idle3 = idle2-idle
    a = float(active3)
    i = float(idle3)
    print(a)
    print(i)
    util = (100*a)/(a+i)
    for i in range (2):
        print('hello')
    return util

print(getData('172.26.1.147',0))

