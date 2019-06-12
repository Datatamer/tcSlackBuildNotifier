package slacknotifications.teamcity.extension;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.Commit;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.settings.SlackNotificationMainConfig;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlackNotificationDMTests {

  SBuildServer sBuildServer = mock(SBuildServer.class);
  WebControllerManager webControllerManager = mock(WebControllerManager.class);
  SlackNotifierSettingsController controller;

  @Before
  public void initializeConfigs(){
    String expectedConfigDirectory = ".";
    ServerPaths serverPaths = mock(ServerPaths.class);
    when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

    PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);

    SlackNotificationMainConfig config = new SlackNotificationMainConfig(serverPaths);
    SlackNotificationPayloadManager payloadManager = new SlackNotificationPayloadManager(sBuildServer);

    controller = new SlackNotifierSettingsController(
      sBuildServer, serverPaths, webControllerManager,
      config, payloadManager, pluginDescriptor);
  }

  private SlackNotification sendMock(boolean sendChannel){
    SlackNotification notification = controller.createMockNotification(
      "tamr",
      "#teamcity_slack_tests",
      "Test Bot",
      "https://hooks.slack.com/services/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",
      SlackNotificationMainConfig.DEFAULT_ICONURL,
      5,
      true,
      true,
      true,
      true,
      "master",
      false,
      true,
      null, null, null, null, sendChannel, true);

    return notification;
  }



  @Test
  public void sendToDefaultChannelTest() throws IOException {

    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "frankjh3", "frankie.holzapfel"));

    SlackNotification notification = controller.createMockNotification(
      "tamr",
      "#teamcity_slack_tests",
      "Test Bot",
      "https://hooks.slack.com/services/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",
      SlackNotificationMainConfig.DEFAULT_ICONURL,
      5,
      true,
      true,
      true,
      true,
      "master",
      false,
      true,
      null, null, null, null, true, false);

    assertNotNull(notification);
  }

  @Test
  public void sendToCommitUsersTest() throws IOException {
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "commiting stuff from frankjh3", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void multipleCommitsOneUserTest() throws IOException {
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "Did some stuff", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void mergePullRequestOtherTest() throws IOException {
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "Merge pull request #15208 from rick/authz.service", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void mergePullRequestSameTest() throws IOException {
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "Merge pull request #15208 from frankjh3/authz.service", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void multipleCommitsOneUserSpacedTest() throws IOException {
    controller.addCommitMockNotification(new Commit("jdajfla", "early commits", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("adijefp", "branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void afterFifthCommitUserTest() throws IOException {
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "early commits", "jdasfj", "fake.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("adijefp", "branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "early commits", "jdasfj", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "early commits", "jdasfj", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "early commits", "jdasfj", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void afterFifthOnlyUserTest() throws IOException {
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void lessThanFiveOthersTest() throws IOException {
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "early commits", "jdasfj", "fake.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "Jimbo", "Jimbo.slack"));
    controller.addCommitMockNotification(new Commit("abb23b4", "Merge of branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("adijefp", "branch xyz", "rick", "rick.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void oneMoreCommitsTest() throws IOException {
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("dsalfhh", "other", "adfaafa", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }

  @Test
  public void nMoreCommitsTest() throws IOException {
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("dsalfhh", "other", "adfaafa", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("dsalfhh", "other", "adfaafa", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "first commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("jdajfla", "second commit", "frankjh3", "frankie.holzapfel"));
    controller.addCommitMockNotification(new Commit("dsalfhh", "other", "adfaafa", "fake.slack"));
    controller.addCommitMockNotification(new Commit("jdajfla", "later commits", "frankjh3", "frankie.holzapfel"));

    assertNotNull(sendMock(true));
  }
}

