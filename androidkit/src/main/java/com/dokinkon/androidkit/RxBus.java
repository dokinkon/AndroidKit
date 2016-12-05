package com.dokinkon.androidkit;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;


@Singleton
public class RxBus {

    @Inject
    protected RxBus() {

    }

    private final Subject<Object, Object> busSubject = new SerializedSubject<>(PublishSubject.create());

    @NonNull
    public <T> Observable<T> subscribe(final Class<T> eventClass) {
        return busSubject.filter(e -> e.getClass().equals(eventClass))
                .map(o -> (T)o);
    }

    public void post(Object event) {
        busSubject.onNext(event);
    }
}
