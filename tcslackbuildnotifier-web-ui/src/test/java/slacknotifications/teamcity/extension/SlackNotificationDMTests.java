package slacknotifications.teamcity.extension;

import org.junit.Test;
import static org.junit.Assert.*;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.settings.SlackNotificationMainConfig;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlackNotificationDMTests {

  SBuildServer sBuildServer = mock(SBuildServer.class);
  WebControllerManager webControllerManager = mock(WebControllerManager.class);

  @Test
  public void sendToSpecifiedChannel() throws IOException {
    String expectedConfigDirectory = ".";
    ServerPaths serverPaths = mock(ServerPaths.class);
    when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

    PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);

    SlackNotificationMainConfig config = new SlackNotificationMainConfig(serverPaths);
    SlackNotificationPayloadManager payloadManager = new SlackNotificationPayloadManager(sBuildServer);

    SlackNotifierSettingsController controller = new SlackNotifierSettingsController(
      sBuildServer, serverPaths, webControllerManager,
      config, payloadManager, pluginDescriptor);

    SlackNotification notification = controller.createMockNotification(
      "tamr",
      "@frankie.holzapfel",
      "Test Bot",
      "https://hooks.slack.com/services/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",
      SlackNotificationMainConfig.DEFAULT_ICONURL,
      5,
      true,
      true,
      true,
      true,
      "master",
      true,
      true,
      null, null, null, null);

    assertNotNull(notification);
  }
}

