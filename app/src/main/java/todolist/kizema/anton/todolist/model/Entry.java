package todolist.kizema.anton.todolist.model;


import java.io.Serializable;

public class Entry implements Serializable {

    public String title;
    public String description;
    public boolean alive = true;//is not cropssed over

    public Entry(String title){
        this(title, "", true);
    }

    public Entry(String title, String descr){
        this(title, descr, true);
    }

    public Entry(String title, String descr, boolean alive){
        this.title = title;
        this.description = descr;
        this.alive = alive;
    }

    public boolean isAlive(){
        return alive;
    }

    @Override
    public boolean equals(Object o) {
        Entry second = (Entry) o;

        if (second.title.equalsIgnoreCase(title) && second.description.equalsIgnoreCase(description))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + description.hashCode() + title.hashCode();
    }

}
