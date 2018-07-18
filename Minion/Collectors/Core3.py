import paramiko
import string
def ssh_cmd(host, cmd):
   ssh = paramiko.SSHClient()
   ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
   ssh.connect(host, username='root', password='netronome')
   session = ssh.get_transport().open_session()
   session.exec_command(cmd)
   stdout = session.makefile('r',-1)
   output = stdout.read().decode('utf-8').split('\n')[:-1]
   ssh.close()
   return output

def getData(host):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(host, username='root', password='netronome')
    session = ssh.get_transport().open_session()
    cmd = "cat /sys/bus/pci/drivers/igb/0*/numa_node | head -n1 | grep [0-9]"
    node = ssh_cmd(host,cmd)[0]
    cmd="lscpu -a -p | awk -F',' -v var=" + node + " '$4 == var {printf \"%s%s\",sep,$1; sep=\",\"} END{print \"\"}'"
    cpu_list = ssh_cmd(host,cmd)[0]
    cpu_num=int(cpu_list[-1])+1
    cmd = "mpstat -P " + cpu_list + " 1 1 | tail -" + str(cpu_num) + " | tr -s" + " ' ' " + "',' " + " | cut -d " + "',' " + "-f2,12"
    usage = ssh_cmd(host,cmd)
    invert = []
    for x in usage:
        two = x.split(',')
        num = two[0]
        usg = 100 - float(two[1])
        new = str(num) + "=" + str(usg)
        invert.append(new)
    string = ""
    for x in invert:
        if x[0]!='0': string = string + "\n"
        string = string + x
    cmd = "echo '" + string + "' > /tmp/cpu.txt"
    ssh_cmd(host,cmd)
    ftp_client=ssh.open_sftp()
    ftp_client.get('/tmp/cpu.txt','cpu.txt')
    ftp_client.close()
    ssh.close()
    tot = 0
    cnt = 0
    for val in usage:
        tot = tot + float(val[2])
        cnt = cnt + 1
    return 100-(tot/cnt)
