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
public class UserInfoUseCase extends UseCase {

    private final DataRepository dataRepository;
    //使用注入，自动生成该UseCase
    @Inject
    public UserInfoUseCase(DataRepository dataRepository,ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.dataRepository=dataRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return dataRepository.userInfo();
    }

    @Override
    public Observable<User> observable() {
        return execute();
    }
}
