package frames;

public final class UserDataSingleton {
    private static UserDataSingleton instance;
    public int value;

    private UserDataSingleton(){
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static UserDataSingleton getInstance(){
        if(instance == null){
            instance = new UserDataSingleton();
        }
        return instance;
    }
}