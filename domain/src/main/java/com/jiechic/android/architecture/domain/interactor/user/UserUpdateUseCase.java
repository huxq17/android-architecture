package com.jiechic.android.architecture.domain.interactor.user;

import com.jiechic.android.architecture.domain.entity.data.User;
import com.jiechic.android.architecture.domain.executor.PostExecutionThread;
import com.jiechic.android.architecture.domain.executor.ThreadExecutor;
import com.jiechic.android.architecture.domain.interactor.UseCase;
import com.jiechic.android.architecture.domain.repository.DataRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * Created by jiechic on 15/10/30.
 */
public class UserUpdateUseCase extends UseCase {

    private final DataRepository dataRepository;

    private User user;
    //使用注入，自动生成该UseCase
    @Inject
    protected UserUpdateUseCase(DataRepository dataRepository,ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.dataRepository=dataRepository;
    }

    public void setParam(User user){
        this.user=user;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return dataRepository.userUpdate(this.user);
    }

    @Override
    public Observable<User> observable() {
        return execute();
    }
}
