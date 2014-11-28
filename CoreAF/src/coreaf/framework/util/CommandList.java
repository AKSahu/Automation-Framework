package coreaf.framework.util;

import java.util.LinkedList;

/**
 * This class contains the list of commands that were run be selenium.
 * 
 * @author A. K. Sahu
 */
public class CommandList {
    
    /**
     * Queue object for collecting commands
     */
    private LinkedList<String> successList = new LinkedList<String>();
    private LinkedList<String> failureList = new LinkedList<String>();
    private LinkedList<String> successTimeStampList = new LinkedList<String>();
    private LinkedList<String> failureTimeStampList = new LinkedList<String>();
            
    /**
     * Default Constructor
     */
    public CommandList() { }
    
    /**
     * CommandList instance
     */
    private static CommandList instance = new CommandList();
    
    /**
     * Return the instance of this object.
     * 
     * @return The instance of this object 
     */
    public static CommandList getInstance() { return instance; }
    
    /**
     * Add a command to the list.
     * 
     * @param s the command that was run 
     */
    public void reportSuccess(String s) { successList.add(s); successTimeStampList.add(DateUtil.getTimeStampOnlyTime()); }    
    public void reportFailure(String s) { failureList.add(s); failureTimeStampList.add(DateUtil.getTimeStampOnlyTime()); }

    
    /**
     * Removes all commands in the list.
     */
    public void clearSuccessList() { successList.clear(); successTimeStampList.clear();}
    public void clearFailureList() { failureList.clear(); failureTimeStampList.clear();}
    
    public void clearCommandLog() {	successList.clear(); successTimeStampList.clear(); failureList.clear(); failureTimeStampList.clear(); }
    
    /**
     * Get all of the commands in the list.
     * 
     * @return 
     */
    public String[] getSuccessList() { 
        
        Object[] objectList = successList.toArray();
        
        String[] stringList = new String[objectList.length];

        for (int i = 0; i < objectList.length; i++) stringList[i] = objectList[i].toString();

        return stringList; 
    
    }
    
    /**
     * Get all of the commands in the list.
     * 
     * @return 
     */
    public String[] getFailureList() { 
        
        Object[] objectList = failureList.toArray();
        
        String[] stringList = new String[objectList.length];

        for (int i = 0; i < objectList.length; i++) stringList[i] = objectList[i].toString();

        return stringList; 
    
    }
    
    /**
     * Get all of the commands in the list.
     * 
     * @return 
     */
    public String[] getSuccessTimeStampList() { 
        
        Object[] objectList = successTimeStampList.toArray();
        
        String[] stringList = new String[objectList.length];

        for (int i = 0; i < objectList.length; i++) stringList[i] = objectList[i].toString();

        return stringList; 
    
    }
    
    /**
     * Get all of the commands in the list.
     * 
     * @return 
     */
    public String[] getFailureTimeStampList() { 
        
        Object[] objectList = failureTimeStampList.toArray();
        
        String[] stringList = new String[objectList.length];

        for (int i = 0; i < objectList.length; i++) stringList[i] = objectList[i].toString();

        return stringList; 
    
    }
    
    /**
     * Get the size of the list.
     * 
     * @return 
     */
    public int getSizeSuccessList() { return successList.size(); }
    public int getSizeFailureList() { return failureList.size(); }
    
    /**
     * Indicates if command list contains no commands
     * 
     * @return 
     */
    public boolean isEmptySuccessList() { return (getSizeSuccessList() == 0); }
    public boolean isEmptyFailureList() { return (getSizeFailureList() == 0); }
            
}