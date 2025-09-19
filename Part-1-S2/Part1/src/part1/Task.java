/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package part1;

/**
 *
 * @author monde
 */

public final class Task {
    private final String taskName;
    private final String taskDescription;
    private final String developerDetails;
    private final int taskDuration;
    private final String taskStatus;
    private static int taskCounter = 0; //  tracks task numbers
    private static int totalHours = 0; //  total task hours
    private final String taskID;

    public Task(String taskName, String taskDescription, String developerDetails, int taskDuration, String taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.developerDetails = developerDetails;
        this.taskDuration = taskDuration;
        this.taskStatus = taskStatus;
        this.taskID = createTaskID();
        totalHours += taskDuration;
    }
    
    public String createTaskID() {
        taskCounter++;
        String taskNamePart = taskName.length() >= 2 ? taskName.substring(0, 2).toUpperCase() : taskName.toUpperCase();
        String developerNamePart = developerDetails.length() >= 3 ? developerDetails.substring(developerDetails.length() - 3).toUpperCase() : developerDetails.toUpperCase();
        return taskNamePart + ":" + taskCounter + ":" + developerNamePart;
    }

    public String printTaskDetails() {
        return String.format(
                "Task Name: %s%nTask Description: %s%nDeveloper Details: %s%nTask Duration: %d hours%nTask Status: %s%nTask ID: %s",
                taskName, taskDescription, developerDetails, taskDuration, taskStatus, taskID
        );
    }

    public static int returnTotalHours() {
        return totalHours;
    }

   
}

