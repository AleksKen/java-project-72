@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.urls.UrlsPage
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <h1>Urls List</h1>
    <a href="${NamedRoutes.rootPath()}">New Car</a>
    @if(page.getUrls().isEmpty())
        <p>Urls list is empty</p>
    @else
        @for(var url : page.getUrls())
            <div>
                <h2>
                    <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                </h2>
            </div>
        @endfor
    @endif
`
)