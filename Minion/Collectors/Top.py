import paramiko
def ssh_cmd(host, cmd):
   ssh = paramiko.SSHClient()
   ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
   ssh.connect(host, username='root', password='netronome')
   session = ssh.get_transport().open_session()
   session.exec_command(cmd)
   stdout = session.makefile('r',-1)
   output = stdout.read().decode('utf-8').split('\n')
   ssh.close()
   return output

def getData(host):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(host, username='root', password='netronome')
    cmd = "cat /sys/bus/pci/drivers/igb/0*/numa_node | head -n1 | grep [0-9]"
    node = ssh_cmd(host,cmd)[0]
    cmd="lscpu -a -p | awk -F',' -v var=" + node + " '$4 == var {printf \"%s%s\",sep,$1; sep=\",\"} END{print \"\"}'"
    cpu_list = ssh_cmd(host,cmd)[0].split(',')
    cmd="nproc"
    cpus = ssh_cmd(host,cmd)[0]
    top = []
    for i in range(int(cpus)):
       top.append([0,0])
    cmd = "ps -eL -o psr,pcpu,pid,cmd | grep -v '%CPU'"
    usage = ssh_cmd(host,cmd)
    for x in range(len(usage)-1):
       temp = usage[x].split()
       cpu = int(temp[0])
       core = float(temp[1])
       pid = int(temp[2])
       if(core>top[cpu][1]):
          top[cpu][0]=str(temp[3])
          top[cpu][1]=core
    string = ""
    for x in cpu_list:
       string = string + str(x) + ":" + str(top[int(x)][0]) + "\n" #":Core["+str(x)+"]"+str(top[int(x)][0]) + "\n"
    cmd = "echo '" + string + "' > /tmp/core.txt"
    ssh_cmd(host,cmd)
    ftp_client=ssh.open_sftp()
    ftp_client.get('/tmp/core.txt','core.txt')
    ftp_client.close()
    ssh.close()
    return
