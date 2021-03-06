package dynamic.type.inferences.notification;

import com.intellij.notification.*;


public class ModelNotLoadedNotification {

    public Notification createInfoNotification() {
        return NotificationGroupManager
                .getInstance()
                .getNotificationGroup("VaDima Notification Group")
                .createNotification(
                        "VaDima plugin info",
                        "Model not loaded",
                        "Predictions for user-defined functions will be available after model load.\n",
                        NotificationType.INFORMATION);
    }

    public Notification createErrorNotification() {
        return NotificationGroupManager
                .getInstance()
                .getNotificationGroup("VaDima Notification Group")
                .createNotification(
                        "VaDima plugin error",
                        "Model was not loaded correctly. Trying to reload.",
                        "Trying to reload...\n",
                        NotificationType.ERROR);
    }
}
