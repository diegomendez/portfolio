package com.wideo.metrics.interceptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import com.google.api.client.util.Maps;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

/**
 * Esta clase lo que hace es determinar el idioma del usuario en base a su
 * ubicacion geografica.
 * Dado que puede no encontrarla, se pone por defecto el idioma "en"
 * 
 * @author diego
 */
public class GeoLangInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = Logger
            .getLogger(GeoLangInterceptor.class);

    DatabaseReader reader = null;

    Map<String, String> countries;
    
    @Value("${cookie.domain}")
    private String cookieDomain;
    
    @Value("${cookie.maxAge}")
    private Integer cookieMaxAge;

    /**
     * Builds the countries map in memory on starting time
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JSONException
     */
    public void init() throws FileNotFoundException, IOException, JSONException {
        if (countries == null) {
            this.countries = Maps.newHashMap();
            // Reading file
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(GeoLangInterceptor.class
                            .getResourceAsStream("/env/countries-info.json")));

            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            // Reading file
            JSONObject jsonObject = new JSONObject(
                    responseStrBuilder.toString());

            JSONArray countriesArray = (JSONArray) jsonObject.get("geonames");

            final int n = countriesArray.length();
            for (int i = 0; i < n; ++i) {
                JSONObject countryObj = countriesArray.getJSONObject(i);
                // Languages format on json example:
                // languages: "pt-BR,es,en,fr",
                String languages = (String) countryObj.get("languages");
                String[] langs = languages.split(",");
                String lang = langs[0];
                if (lang.contains("-")) {
                    String[] langWithoutCountry = lang.split("-");
                    lang = langWithoutCountry[0];
                }
                String isoCode = (String) countryObj.get("countryCode");
                this.countries.put(isoCode.toLowerCase(), lang.toLowerCase());
            }

        }

    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        try {
            Cookie cookieCountry = WebUtils.getCookie(request, "countryID");
            if (cookieCountry == null) {
                if (reader == null) {
                    // A File object pointing to your GeoIP2 or GeoLite2
                    // database
                    File database = new File(
                            "/data/wideo.co/geo-maxmind/GeoLite2-City.mmdb");
                    if (database.exists()) {

                        // This creates the DatabaseReader object, which should
                        // be
                        // reused across lookups.
                        reader = new DatabaseReader.Builder(database).build();
                    }
                }

                if (reader != null) {
                    String ip = request.getHeader("X-Forwarded-For");
                    
                    if (ip != null && !"127.0.0.1".equals(ip)) {
                        CityResponse responseIp = reader.city(InetAddress
                                .getByName(ip));
                        String isoCode = responseIp.getCountry().getIsoCode()
                                .toLowerCase();
                        setCountry(response, isoCode);
                        
                    }
                }
            } else {
                request.setAttribute("countryID", cookieCountry.getValue());
            }
        }
        catch (Exception e) {
            LOGGER.debug("Ocurrio un problema mientras se detectaba el lenguaje");
        }
        return super.preHandle(request, response, handler);
    }

    private void setCountry(HttpServletResponse response, String country) {
        CookieGenerator cg = new CookieGenerator();
        cg.setCookiePath("/");
        cg.setCookieMaxAge(this.cookieMaxAge);
        cg.setCookieName("countryID");
        cg.setCookieDomain(this.cookieDomain);
        cg.addCookie(response, country);
    }

}
