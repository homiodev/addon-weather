package org.homio.bundle.weather.widget;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.homio.bundle.weather.setting.WeatherApiKeySetting;
import org.homio.bundle.weather.setting.WeatherUnitSetting;
import org.springframework.stereotype.Component;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.ui.field.UIField;
import org.homio.bundle.api.ui.field.UIFieldNumber;
import org.homio.bundle.api.ui.field.UIFieldType;
import org.homio.bundle.api.widget.JavaScriptBuilder;
import org.homio.bundle.api.widget.WidgetJSBaseTemplate;
import org.homio.bundle.hquery.hardware.network.NetworkHardwareRepository;

@Getter
@Component
@RequiredArgsConstructor
public class OpenWeatherWidgetTemplate implements WidgetJSBaseTemplate {

  private final EntityContext entityContext;
  private final NetworkHardwareRepository networkHardwareRepository;

  @UIField(order = 1, type = UIFieldType.Slider, label = "weather.type")
  @UIFieldNumber(min = 1, max = 24)
  private Integer id;

  @UIField(order = 2, label = "city")
  private String city_name;

  @Override
  public String getIcon() {
    return "fas fa-sun";
  }

  @Override
  @SneakyThrows
  public void createWidget(JavaScriptBuilder javaScriptBuilder) {
    javaScriptBuilder.css("widget-left", "margin: 0 !important;");
    javaScriptBuilder.setJsonReadOnly();
    String containerId = "cow-" + System.currentTimeMillis();
    javaScriptBuilder
        .jsonParam("id", "15")
        .jsonParam("city_name", networkHardwareRepository.getIpGeoLocation(
            networkHardwareRepository.getOuterIpAddress()).getCity());

    javaScriptBuilder.readyOnClient().window(window -> {
          window.array("myWidgetParam")
              .value("id", "${id}")
              .value("city_name", "${city_name}")
              .value("containerid", containerId)
              .value("units", (JavaScriptBuilder.ProxyEntityContextValue) entityContext -> entityContext.setting()
                  .getValue(WeatherUnitSetting.class))
              .value("appid", (JavaScriptBuilder.ProxyEntityContextValue) entityContext -> entityContext.setting()
                  .getValue(WeatherApiKeySetting.class));
        })
        .addGlobalScript("//openweathermap.org/themes/openweathermap/assets/vendor/owm/js/weather-widget-generator.js");

    javaScriptBuilder.jsContent().div(style -> style.id(containerId), div -> {
    });
  }
}
