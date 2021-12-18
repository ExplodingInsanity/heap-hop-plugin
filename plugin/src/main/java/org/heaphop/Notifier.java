package org.heaphop;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;

public class Notifier {

    public static void notifyError(String content) {
        if (SharedData.getInstance().project == null) {
            return;
        }
        NotificationGroupManager.getInstance().getNotificationGroup("Notification Group")
                .createNotification(content, NotificationType.ERROR)
                .notify(SharedData.getInstance().project);
    }

    public static void notifyInformation(String content) {
        if (SharedData.getInstance().project == null) {
            return;
        }
        NotificationGroupManager.getInstance().getNotificationGroup("Notification Group")
                .createNotification(content, NotificationType.INFORMATION)
                .notify(SharedData.getInstance().project);
    }
}
