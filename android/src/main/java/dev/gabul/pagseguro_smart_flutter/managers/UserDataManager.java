package dev.gabul.pagseguro_smart_flutter.managers;



import dev.gabul.pagseguro_smart_flutter.user.UserData;
import dev.gabul.pagseguro_smart_flutter.user.usecase.EditUserUseCase;
import dev.gabul.pagseguro_smart_flutter.user.usecase.GetUserUseCase;
import dev.gabul.pagseguro_smart_flutter.user.usecase.NewUserUseCase;
import io.reactivex.Observable;
import io.reactivex.Single;

public class UserDataManager {

    private GetUserUseCase mGetUser;
    private NewUserUseCase mNewUser;
    private EditUserUseCase mEditUser;

    public UserDataManager(GetUserUseCase getUser, NewUserUseCase newUser, EditUserUseCase mEditUser) {
        this.mGetUser = getUser;
        this.mNewUser = newUser;
        this.mEditUser = mEditUser;
    }

    public Single<UserData> getUserData(){
        return mGetUser.getUser();
    }

    public Observable<Integer> writeUserData(UserData userData){
        return mNewUser.writeUserInNFcCard(userData);
    }

    public Observable<Integer> reWriteUserData(UserData userData){
        return mEditUser.reWriteUserInNFcCard(userData);
    }
}
