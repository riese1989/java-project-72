@import hexlet.code.dto.pages.UrlDataCheckPage
@import hexlet.code.utils.NamedRoutes
@import java.time.format.DateTimeFormatter
@param UrlDataCheckPage page

@template.layout.page(
content = @`
    @if(page.getMessageRecord() != null)
        <div class="rounded-0 m-0 alert alert-dismissible fade show alert-info" role="alert">
            <p class="m-0">${page.getMessageRecord().getMessage()}</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    <div class="container-lg mt-5">
        <h1>Сайт: ${page.getName()}</h1>

        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>ID</td>
                <td>${page.getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${page.getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${page.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</td>
            </tr>
            </tbody>
        </table>
        <h2 class="mt-5">Проверки</h2>
        <form method="post" action="${NamedRoutes.checkUrlPath(page.getId())}">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>
        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th>ID</th>
                <th>Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th>Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            @if(page.getChecks() != null)
                @for (var check : page.getChecks())
                    <tr>
                        <td>${page.getChecks().indexOf(check) + 1}</td>
                        <td>${check.getCodeAnswer()}</td>
                        <td>${check.getTitle()}</td>
                        <td>${check.getH1()}</td>
                        <td>${check.getDescription()}</td>
                        <td>${check.getDateCheck().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</td>
                    </tr>
                @endfor
            @endif
            </tbody>
        </table>
    </div>


`
)
