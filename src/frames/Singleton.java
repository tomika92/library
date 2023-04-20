package frames;

public final class Singleton {
    private static Singleton instance;
    public int value;

    private Singleton(){
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }
}
