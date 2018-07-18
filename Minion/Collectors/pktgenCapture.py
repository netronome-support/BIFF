import subprocess
import os.path
import os
import time
import re
import paramiko

def getData(path):
    cmd = "virsh net-dhcp-leases default | grep ipv4 | head -1 | cut -d ' ' -f13 | cut -d '/' -f1"
    vm_ip = str(subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE).stdout.read())
    vm = vm_ip.split('\'')[1].split('\\')[0]    
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    try:
        ssh.connect(vm, username='root', password='netronome')
        session = ssh.get_transport().open_session()
        #cmd = 'ssh -A -t spock.netronome.com "tail -1 '+ path + '"'
        cmd = 'tail -1 ' + path
        # ---
        #print(cmd)
        session.exec_command(cmd)
        stdout = session.makefile('r',-1)
        stdout_text = stdout.read()
        ssh.close()
        line = str(stdout_text)[2:-3]
        data = line.split(',')
        retval = []
        for val in data:
            retval.append(val.lstrip())
        return retval
    except:
        return 0

def getFramesize(path):
    data = getData(path)
    try:
        return int(data[2]) if data[2]!='-nan' else 0
    except:
        return 0

def getFramerate(path):
    data = getData(path)
    try:
        return float(data[3])*1e-6 if data[2]!='-nan' else 0
    except:
        return 0

def getFramedata(path):
    data = getData(path)
    try:
        return float(data[4])*1e-3 if data[2]!='-nan' else 0
    except:
        return 0

#print(getFramesize('/root/capture.txt'))
#print(getFramerate('172.26.1.136', '/root/IVG_folder/capture.txt'))
#print(getFramedata('172.26.1.136', '/root/IVG_folder/capture.txt'))
