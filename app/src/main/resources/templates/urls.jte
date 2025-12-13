@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
content = @`
    @if(page.getMessageRecord().getId() == 1)
        <div class="rounded-0 m-0 alert alert-dismissible fade show alert-success" role="alert">
            <p class="m-0">${page.getMessageRecord().getMessage()}</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    @if(page.getMessageRecord().getId() == 2)
        <div class="rounded-0 m-0 alert alert-dismissible fade show alert-info" role="alert">
            <p class="m-0">${page.getMessageRecord().getMessage()}</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    <form action="${NamedRoutes.urlsPath()}" method="post">
        <label for="url">Введите URL:</label>
        <input type="text" id="url" name="url" placeholder="https://site.ru"/>
        <button type="submit">Отправить</button>
    </form>
`
)
