package ello.dependencies.component;
/**
 * @package com.trioangle.igniter
 * @subpackage dependencies.component
 * @category AppComponent
 * @author Trioangle Product Team
 * @version 1.0
 **/

import javax.inject.Singleton;

import dagger.Component;
import ello.adapters.chat.ChatConversationListAdapter;
import ello.adapters.chat.MessageUserListAdapter;
import ello.adapters.chat.NewMatchesListAdapter;
import ello.adapters.chat.UnmatchReasonListAdapter;
import ello.adapters.chat.UserListAdapter;
import ello.adapters.main.ProfileSliderAdapter;
import ello.adapters.matches.MatchesSwipeAdapter;
import ello.adapters.profile.EditProfileImageListAdapter;
import ello.adapters.profile.EnlargeSliderAdapter;
import ello.adapters.profile.IgniterSliderAdapter;
import ello.adapters.profile.LocationListAdapter;
import ello.backgroundtask.ImageCompressAsyncTask;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.dependencies.module.AppContainerModule;
import ello.dependencies.module.ApplicationModule;
import ello.dependencies.module.NetworkModule;
import ello.pushnotification.MyFirebaseInstanceIDService;
import ello.pushnotification.MyFirebaseMessagingService;
import ello.pushnotification.NotificationUtils;
import ello.swipedeck.Utility.SwipeListener;
import ello.utils.CommonMethods;
import ello.utils.DateTimeUtility;
import ello.utils.ImageUtils;
import ello.utils.RequestCallback;
import ello.utils.WebServiceUtils;
import ello.views.action.DeleteAccountActivity;
import ello.views.action.ReportUserActivity;
import ello.views.action.UnmatchUserActivity;
import ello.views.chat.ChatConversationActivity;
import ello.views.chat.ChatFragment;
import ello.views.chat.CreateGroupActivity;
import ello.views.chat.MatchUsersActivity;
import ello.views.main.BoostDialogActivity;
import ello.views.main.EmailActivity;
import ello.views.main.HomeActivity;
import ello.views.main.IgniterGoldActivity;
import ello.views.main.IgniterPageFragment;
import ello.views.main.IgniterPlusDialogActivity;
import ello.views.main.IgniterPlusSliderFragment;
import ello.views.main.LoginActivity;
import ello.views.main.SplashActivity;
import ello.views.main.TutorialFragment;
import ello.views.main.UserNameActivity;
import ello.views.main.VerificationActivity;
import ello.views.main.VerifyEmailActivity;
import ello.views.profile.AddLocationActivity;
import ello.views.profile.EditProfileActivity;
import ello.views.profile.EnlargeProfileActivity;
import ello.views.profile.GetIgniterPlusActivity;
import ello.views.profile.ProfileFragment;
import ello.views.profile.SettingsActivity;
import ello.views.signup.BirthdayFragment;
import ello.views.signup.EmailFragment;
import ello.views.signup.FacebookEmailActivity;
import ello.views.signup.OneTimePwdFragment;
import ello.views.signup.PasswordFragment;
import ello.views.signup.PhoneNumberFragment;
import ello.views.signup.ProfilePickFragment;
import ello.views.signup.ResendCodeFragment;
import ello.views.signup.SignUpActivity;

/*****************************************************************
 App Component
 ****************************************************************/
@Singleton
@Component(modules = {NetworkModule.class, ApplicationModule.class, AppContainerModule.class})
public interface AppComponent {
    // ACTIVITY

    void inject(SplashActivity splashActivity);
    void inject(ReportUserActivity reportUserActivity);
    void inject(UnmatchUserActivity unmatchUserActivity);
    void inject(DeleteAccountActivity deleteAccountActivity);
    void inject(HomeActivity homeActivity);
    void inject(FacebookEmailActivity facebookEmailActivity);
    void inject(SettingsActivity settingsActivity);

    void inject(EditProfileActivity editProfileActivity);

    void inject(GetIgniterPlusActivity getIgniterPlusActivity);

    void inject(SignUpActivity signUpActivity);
    void inject(VerifyEmailActivity verifyEmailActivity);

    void inject(EnlargeProfileActivity enlargeProfileActivity);

    void inject(MatchUsersActivity matchUsersActivity);

    void inject(ChatConversationActivity chatConversationActivity);

    void inject(CreateGroupActivity createGroupActivity);

    void inject(LoginActivity loginActivity);

    void inject(VerificationActivity verificationActivity);

    void inject(UserNameActivity userNameActivity);

    void inject(AddLocationActivity addLocationActivity);
    void inject(EmailActivity emailActivity);

    // Fragments
    void inject(ProfileFragment profileFragment);

    void inject(IgniterPageFragment igniterPageFragment);

    void inject(ChatFragment chatFragment);

    void inject(ProfilePickFragment profilePickFragment);

    void inject(EmailFragment emailFragment);

    void inject(PasswordFragment passwordFragment);

    void inject(BirthdayFragment birthdayFragment);

    void inject(TutorialFragment tutorialFragment);

    void inject(PhoneNumberFragment phoneNumberFragment);

    void inject(ResendCodeFragment resendCodeFragment);

    void inject(OneTimePwdFragment oneTimePwdFragment);

    void inject(IgniterPlusDialogActivity igniterPlusDialogActivity);

    void inject(BoostDialogActivity boostDialogActivity);

    void inject(IgniterPlusSliderFragment igniterPlusSliderFragment);

    // Utilities
    void inject(RunTimePermission runTimePermission);

    void inject(SessionManager sessionManager);

    void inject(ImageUtils imageUtils);

    void inject(CommonMethods commonMethods);

    void inject(ProfileSliderAdapter profileSliderAdapter);

    void inject(RequestCallback requestCallback);

    void inject(DateTimeUtility dateTimeUtility);

    void inject(WebServiceUtils webServiceUtils);

    // Adapters
    void inject(IgniterSliderAdapter igniterSliderAdapter);

    void inject(NewMatchesListAdapter newMatchesListAdapter);

    void inject(MessageUserListAdapter messageUserListAdapter);

    void inject(EnlargeSliderAdapter enlargeSliderAdapter);

    void inject(EditProfileImageListAdapter editProfileImageListAdapter);

    void inject(ChatConversationListAdapter chatConversationListAdapter);

    void inject(UnmatchReasonListAdapter unmatchReasonListAdapter);

    void inject(UserListAdapter chatUserListAdapter);

    void inject(MatchesSwipeAdapter matchesSwipeAdapter);

    void inject(SwipeListener swipeListener);

    void inject(LocationListAdapter locationListAdapter);

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

    void inject(MyFirebaseInstanceIDService myFirebaseInstanceIDService);


    // AsyncTask
    void inject(ImageCompressAsyncTask imageCompressAsyncTask);

    void inject(IgniterGoldActivity igniterGoldActivity);

    void inject(NotificationUtils notificationUtils);


}
