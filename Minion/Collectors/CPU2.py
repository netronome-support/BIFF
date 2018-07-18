import paramiko
import time

def getData(host):
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(host, username='root', password='netronome')
    session = client.get_transport().open_session()
    cmd = 'top -bn 2 -d 0.01 | grep ' + 'Cpu' + ' | tail -n 1 | gawk ' + '\'{print $2+$4+$6}\''
    session.exec_command(cmd)
    stdout = session.makefile('r',-1)
    stdout_text = stdout.read()
    client.close()
    retval = str(stdout_text)[2:-3]
    return retval
