﻿##############################################################################
#  Copyright (c) 2016 Intel Corporation
# 
# Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
##############################################################################
#    File Abstract: 
#    Thread Manager
#
##############################################################################

import xml.dom.minidom
from xml.parsers.expat import ExpatError
from Helpers import  Log
from Util import Sleep
import threading
import sys
import time
import traceback

def Yield():
    time.sleep(.000001) # a sleep of 0 doesn't actually do anything

######################
# wrapper for worker thread
# takes care of threading and signalling, give ia a user fn to call
class ThreadClass():
    def __init__(self,strName,userThreadProc,userData,SimpleThread=False):
        self.__Running = False
        self.__StopFlag = False
        self.__Stopped = True
        self.__ID = strName
        self.__userThreadProc = userThreadProc
        self.__userData = userData
        self.__workerThread = None
        self.__ReportedStopped = False
        self.__SimpleThread = SimpleThread

    # internal thread proc - setup some flags and call the real worker function
    def __ThreadProc(self):
        self.__Stopped = False
        try:
            if not self.__SimpleThread:
                self.__userThreadProc(self.StopSignalled,self.__userData) #go run the real worker thread, passing fn ptr to check to see if signalled to exit
            else:
                self.__userThreadProc(self.__userData) #go run the simple thread (should not loop - only used in Actor right now)
        except Exception as ex:
            Log.getLogger().error("Uncaught error in thread [" + self.__ID + "]: " + str(ex))
            Log.getLogger().error(traceback.print_exc())
        self.__Running = False
        self.__StopFlag = False
        self.__Stopped = True

    #starts the worker thread
    def Start(self):
        if True == self.__StopFlag:
            Log.getLogger().error("Tried to restart Thread ["+self.__ID+"] before it had stopped.")
            return

        if True == self.__Running:
            Log.getLogger().error("Tried to restart Thread ["+self.__ID+"] that is already running.")
            return

        self.__Running = True
        self.__StopFlag = False
        self.__workerThread = threading.Thread(target=self.__ThreadProc)  
        
        self.__workerThread.start() 

    #stops the worker thread
    def Stop(self):
        if False == self.IsRunning():
            Log.getLogger().error("Tried to stop Thread ["+self.__ID+"] that is not running.")
            return
        self.SignalStop()

        while False == self.IsStopped():
            Sleep.Sleep(.01)

    def ReportStopped(self):
        if False == self.__ReportedStopped:
            self.__ReportedStopped = True
            Log.getLogger().debug("Thread [" + self.__ID +"] has stopped")

    #Set flag to stop the thread
    def SignalStop(self):
       self.__StopFlag = True

    #return True if thread has been signalled to stop, else False
    def StopSignalled(self):
        return self.__StopFlag

    #return True if the thread is Running, else False
    def IsRunning(self):
        return self.__Running

    #return True if the thread is Stopped, else False
    def IsStopped(self):
        return self.__Stopped

###########################################
#thread manager class, creates, stops, starts thread   
# currently can't remove a thread - no need to
class ThreadManager():
    _instance = None
    def __init__(self):
        if ThreadManager._instance == None: # singleton pattern
            ThreadManager._instance = self
            self.__initialize()
            ThreadManager._instance = self
            

        else:
            self = GetThreadManager()
    
    def __initialize(self):
        self.__threadList = {}
        #multiprocessing.set_start_method('spawn')

    def ThreadExists(self,strName):
        return None != self.__GetThread(strName)
        
    def CreateThread(self,strName,userThreadProc,userData=None): 
        if strName in self.__threadList:
            Log.getLogger().error("Tried to create new thread with duplicate ID: " + strName)
            return False

        objThread = ThreadClass(strName,userThreadProc,userData)
        self.__threadList[strName] = objThread
        return True

    #Internal routine to get thread from collection
    def __GetThread(self, strName):
        if strName in self.__threadList:
            return self.__threadList[strName]

        Log.getLogger().error("Tried to get thread with ID: " + strName)
        return None

    #starts the specific named thread
    def StartThread(self,strName):
        objThread = self.__GetThread(strName)
        if None != objThread:
            objThread.Start()

        else:
            Log.getLogger().error("StartThread called with unknown thread name: " + strName)

    #stops the specific named thread
    def StopThread(self,strName):
        objThread = self.__GetThread(strName)
        if None != objThread:
            objThread.Stop()

        else:
            Log.getLogger().error("StartThread called with unknown thread name: " + strName)

    #stops all threads
    def StopAllThreads(self):
        for key in self.__threadList:
            objThread = self.__threadList[key]
            if True == objThread.IsRunning():
                objThread.SignalStop()

        allDone = False
        while False == allDone:
            allDone = True
            for key in self.__threadList:
                objThread = self.__threadList[key]
                if True == objThread.IsRunning():
                    allDone = False
                else:
                    objThread.ReportStopped()

            Sleep.Sleep(.01) # sleep for 100ms

def GetThreadManager():
    if None == ThreadManager._instance:
        return ThreadManager()
    return ThreadManager._instance

# Down a dirty thread wrapper using existing framwork, but doesn't require a name
# used for fire off a thread to perform a task/Actor
def RunWorkerThread(userThreadProc,userData):
    # create a worker thread, but don't register it, it's 1 and done
    objThread = ThreadClass("Tyler Is My Bud!",userThreadProc,userData,True)
    objThread.Start()
