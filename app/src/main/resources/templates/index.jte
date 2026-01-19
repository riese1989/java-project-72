@import hexlet.code.dto.pages.UrlsPage
@import hexlet.code.utils.NamedRoutes
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
