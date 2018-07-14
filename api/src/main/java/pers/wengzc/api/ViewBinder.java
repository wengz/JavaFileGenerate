package pers.wengzc.api;

public interface ViewBinder<T> {
    void bindView(T host, Object object, ViewFinder viewFinder);

    void unBindView(T host);
}
