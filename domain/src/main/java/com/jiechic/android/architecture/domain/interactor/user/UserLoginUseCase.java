package com.jiechic.android.architecture.domain.interactor.user;

import com.jiechic.android.architecture.domain.entity.data.User;
import com.jiechic.android.architecture.domain.entity.request.Login;
import com.jiechic.android.architecture.domain.executor.PostExecutionThread;
import com.jiechic.android.architecture.domain.executor.ThreadExecutor;
import com.jiechic.android.architecture.domain.interactor.UseCase;
import com.jiechic.android.architecture.domain.repository.DataRepository;
import rx.Observable;

import javax.inject.Inject;

/**
 * Created by jiechic on 15/10/30.
 */
public class UserLoginUseCase extends UseCase {

    private final DataRepository dataRepository;

    public Login login;
    //使用注入，自动生成该UseCase
    @Inject
    public UserLoginUseCase(DataRepository dataRepository,ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.dataRepository=dataRepository;
    }

    public void setParam(Login login){
        this.login=login;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return dataRepository.userLogin(this.login);
    }

    @Override
    public Observable<User> observable() {
        return execute();
    }
}
