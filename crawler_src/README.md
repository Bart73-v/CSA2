# Web Crawler with Selenium

This project is a web crawler developed using Java and Selenium. The objective of this project is to crawl the top 500 websites, accept or reject cookies based on user input, and save the HTTP request and response headers and metadata into a JSON file for each visit. Additionally, this crawler should be able to take screenshots of the viewport after page load.

## Requirements

- Java 8 or higher
- Selenium

### Binary

See the [releases] page to download the latest binaries.

[releases]: <https://github.com/Bart73-v/CSA2/releases/>

## Usage

This web crawler can be run using the following command line parameters:

- `-u`: specifies a single URL/domain to crawl
- `-i`: specifies a file containing a list of URLs/domains to crawl, with each URL/domain on a separate line
- `--accept`: specifies the "accept" consent mode
- `--noop`: specifies the "noop" consent mode

Examples:

```sh
java -jar web-crawler.jar -u https://example.com --accept
java -jar web-crawler.jar -i urls.txt --noop
```

## Outputs

For each page visit, the crawler saves the following information into a JSON file:

- `website_domain`: the domain listed in the website list (with "https://" prepended to convert domains into URLs)
- `post_pageload_url`: the URL after the page load
- `pageload_start_ts`: the Unix timestamp marking the beginning of the page load
- `pageload_end_ts`: the Unix timestamp marking the end of the page load
- `requests`: a list of requests containing the URL and the header names and values
- Other information necessary for the analyses below

Screenshots are saved in the following format:

- Crawl-accept: `example.com_accept_pre_consent.png`, `example.com_accept_post_consent.png`
- Crawl-noop: `example.com_noop.png`

## License

This project is licensed under the MIT License - see the LICENSE file for details.
