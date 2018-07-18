for i in range(0,48):
#    line = '<Collector ID=' + '"CPU' + str(i) + '" SendOnlyOnChange=' + '"False" ' + 'ProcessThread="' + str(i) + '">'
#    print(line)
#    print('\t<Executable>Collectors/Core.py</Executable>')
#    print('\t<Param>getData</Param>')
#    print('\t<Param>$(stingray)</Param>')
#    print('\t<Param>'+str(i)+'</Param>')
#    print('</Collector>')
#    print('')
    line = '<MinionSrc ID="CPU' + str(i) + '" Namespace="DPDK-Pktgen" SeriesID="Series' + str(i+1) + '"/>'
    print(line)


#<MinionSrc ID="CPU0" Namespace="DPDK-Pktgen" SeriesID="Series1"/>
