import spock.lang.Specification

//import java.net.Proxy     // <- This is what IntelliJ IDEA resolves
//import groovy.util.Proxy

class HttpConnectionMockTest extends Specification {
  def test() {
    given: "a mock connection manager, returning a mock connection with a predefined response code"
    ConnectionManager connectionManager = Mock() {
      openConnection(_, _) >> Mock(HttpURLConnection) {
        getResponseCode() >> 200
      }
    }

    and: "an object under test using mock proxy, real DTO and mock connection manager"
    def underTest = new UnderTest(Mock(Proxy), new ProxySettingDTO(), connectionManager)

    expect: "method under test returns expected response"
    underTest.getConnectionResponseCode() == 200
  }

  static class UnderTest {
    private Proxy proxy
    private ProxySettingDTO proxySettingDTO
    private ConnectionManager connectionManager

    UnderTest(Proxy proxy, ProxySettingDTO proxySettingDTO, ConnectionManager connectionManager) {
      this.proxy = proxy
      this.proxySettingDTO = proxySettingDTO
      this.connectionManager = connectionManager
    }

    int getConnectionResponseCode() {
      URL url = new URL(proxySettingDTO.getTestUrl())
      HttpURLConnection connection = (HttpURLConnection) connectionManager.openConnection(url, proxy)
      connection.setRequestMethod("GET")
      connection.setUseCaches(false)
      connection.getResponseCode()
    }
  }

  static class ProxySettingDTO {
    String getTestUrl() {
      "https://scrum-master.de"
    }
  }

  static class ConnectionManager {
    URLConnection openConnection(URL url, Proxy proxy) {
      url.openConnection(proxy)
    }
  }
}
