package com.hua.netfloatview;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Jaco on 2019/2/21.
 */
public class EventPostUtil {
    private static class Singleton {
        private static final EventPostUtil INSTANCE = new EventPostUtil();
    }

    public static EventPostUtil getInstance() {
        return EventPostUtil.Singleton.INSTANCE;
    }

    private Subject<Object> subject;

    private EventPostUtil() {
        subject = PublishSubject.create().toSerialized();
    }

    public void post(Object object) {
        if (object != null) {
            subject.onNext(object);
        }
    }

    public <T> Flowable<T> toObservable(Class<T> clazz) {
        return subject.toFlowable(BackpressureStrategy.ERROR).filter(o -> {
            return o.getClass().isAssignableFrom(clazz);
        }).cast(clazz);
    }
}
