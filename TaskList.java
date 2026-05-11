package FinalProject;

class TaskList {
    TaskNode head;
    
    public void add(Task task) {
        TaskNode newNode = new TaskNode(task);
        if (head == null) { head = newNode; } 
        else {
            TaskNode current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }
}