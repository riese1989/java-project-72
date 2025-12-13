@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
    content = @`
    <form action="${NamedRoutes.urlsPath()}" method="post">
        <label for="url">Введите URL:</label>
        <input type="text" id="url" name="url" placeholder="https://site.ru"/>
        <button type="submit">Отправить</button>
    </form>
    `
)
