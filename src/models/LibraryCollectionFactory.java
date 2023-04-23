package models;

public class LibraryCollectionFactory {
    public enum CollectionType{
        BOOK, MAGAZINE, AUDIOBOOK, FILM;
    }
    private LibraryCollectionFactory() {}
    public static LibraryCollection getType(CollectionType collectionType){
        switch (collectionType){
            case BOOK:
                return new Book();
            case MAGAZINE:
                return new Magazine();
            case AUDIOBOOK:
                return new Audiobook();
            case FILM:
                return new Film();
            default:
                return null;
        }
    }
}
