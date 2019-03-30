package com.daddarioc.tidbits.web.rest;

import com.daddarioc.tidbits.TidbitsApp;
import com.daddarioc.tidbits.domain.Category;
import com.daddarioc.tidbits.domain.Tidbit;
import com.daddarioc.tidbits.repository.TidbitRepository;
import com.daddarioc.tidbits.service.TidbitQueryService;
import com.daddarioc.tidbits.service.TidbitService;
import com.daddarioc.tidbits.service.dto.TidbitDTO;
import com.daddarioc.tidbits.service.mapper.TidbitMapper;
import com.daddarioc.tidbits.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.daddarioc.tidbits.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TidbitResource REST controller.
 *
 * @see TidbitResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TidbitsApp.class)
public class TidbitResourceIntTest {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    @Autowired
    private TidbitRepository tidbitRepository;

    @Autowired
    private TidbitMapper tidbitMapper;

    @Autowired
    private TidbitService tidbitService;

    @Autowired
    private TidbitQueryService tidbitQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTidbitMockMvc;

    private Tidbit tidbit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TidbitResource tidbitResource = new TidbitResource(tidbitService, tidbitQueryService);
        this.restTidbitMockMvc = MockMvcBuilders.standaloneSetup(tidbitResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tidbit createEntity(EntityManager em) {
        Tidbit tidbit = new Tidbit()
            .content(DEFAULT_CONTENT)
            .author(DEFAULT_AUTHOR)
            .source(DEFAULT_SOURCE)
            .url(DEFAULT_URL);
        return tidbit;
    }

    @Before
    public void initTest() {
        tidbit = createEntity(em);
    }

    @Test
    @Transactional
    public void createTidbit() throws Exception {
        int databaseSizeBeforeCreate = tidbitRepository.findAll().size();

        // Create the Tidbit
        TidbitDTO tidbitDTO = tidbitMapper.toDto(tidbit);
        restTidbitMockMvc.perform(post("/api/tidbits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tidbitDTO)))
            .andExpect(status().isCreated());

        // Validate the Tidbit in the database
        List<Tidbit> tidbitList = tidbitRepository.findAll();
        assertThat(tidbitList).hasSize(databaseSizeBeforeCreate + 1);
        Tidbit testTidbit = tidbitList.get(tidbitList.size() - 1);
        assertThat(testTidbit.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testTidbit.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testTidbit.getSource()).isEqualTo(DEFAULT_SOURCE);
        assertThat(testTidbit.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    public void createTidbitWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tidbitRepository.findAll().size();

        // Create the Tidbit with an existing ID
        tidbit.setId(1L);
        TidbitDTO tidbitDTO = tidbitMapper.toDto(tidbit);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTidbitMockMvc.perform(post("/api/tidbits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tidbitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tidbit in the database
        List<Tidbit> tidbitList = tidbitRepository.findAll();
        assertThat(tidbitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = tidbitRepository.findAll().size();
        // set the field null
        tidbit.setContent(null);

        // Create the Tidbit, which fails.
        TidbitDTO tidbitDTO = tidbitMapper.toDto(tidbit);

        restTidbitMockMvc.perform(post("/api/tidbits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tidbitDTO)))
            .andExpect(status().isBadRequest());

        List<Tidbit> tidbitList = tidbitRepository.findAll();
        assertThat(tidbitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTidbits() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList
        restTidbitMockMvc.perform(get("/api/tidbits?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tidbit.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR.toString())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())));
    }
    
    @Test
    @Transactional
    public void getTidbit() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get the tidbit
        restTidbitMockMvc.perform(get("/api/tidbits/{id}", tidbit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tidbit.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR.toString()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()));
    }

    @Test
    @Transactional
    public void getAllTidbitsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where content equals to DEFAULT_CONTENT
        defaultTidbitShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the tidbitList where content equals to UPDATED_CONTENT
        defaultTidbitShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllTidbitsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultTidbitShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the tidbitList where content equals to UPDATED_CONTENT
        defaultTidbitShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllTidbitsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where content is not null
        defaultTidbitShouldBeFound("content.specified=true");

        // Get all the tidbitList where content is null
        defaultTidbitShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    public void getAllTidbitsByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where author equals to DEFAULT_AUTHOR
        defaultTidbitShouldBeFound("author.equals=" + DEFAULT_AUTHOR);

        // Get all the tidbitList where author equals to UPDATED_AUTHOR
        defaultTidbitShouldNotBeFound("author.equals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    public void getAllTidbitsByAuthorIsInShouldWork() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where author in DEFAULT_AUTHOR or UPDATED_AUTHOR
        defaultTidbitShouldBeFound("author.in=" + DEFAULT_AUTHOR + "," + UPDATED_AUTHOR);

        // Get all the tidbitList where author equals to UPDATED_AUTHOR
        defaultTidbitShouldNotBeFound("author.in=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    public void getAllTidbitsByAuthorIsNullOrNotNull() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where author is not null
        defaultTidbitShouldBeFound("author.specified=true");

        // Get all the tidbitList where author is null
        defaultTidbitShouldNotBeFound("author.specified=false");
    }

    @Test
    @Transactional
    public void getAllTidbitsBySourceIsEqualToSomething() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where source equals to DEFAULT_SOURCE
        defaultTidbitShouldBeFound("source.equals=" + DEFAULT_SOURCE);

        // Get all the tidbitList where source equals to UPDATED_SOURCE
        defaultTidbitShouldNotBeFound("source.equals=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    public void getAllTidbitsBySourceIsInShouldWork() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where source in DEFAULT_SOURCE or UPDATED_SOURCE
        defaultTidbitShouldBeFound("source.in=" + DEFAULT_SOURCE + "," + UPDATED_SOURCE);

        // Get all the tidbitList where source equals to UPDATED_SOURCE
        defaultTidbitShouldNotBeFound("source.in=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    public void getAllTidbitsBySourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where source is not null
        defaultTidbitShouldBeFound("source.specified=true");

        // Get all the tidbitList where source is null
        defaultTidbitShouldNotBeFound("source.specified=false");
    }

    @Test
    @Transactional
    public void getAllTidbitsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where url equals to DEFAULT_URL
        defaultTidbitShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the tidbitList where url equals to UPDATED_URL
        defaultTidbitShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllTidbitsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where url in DEFAULT_URL or UPDATED_URL
        defaultTidbitShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the tidbitList where url equals to UPDATED_URL
        defaultTidbitShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllTidbitsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        // Get all the tidbitList where url is not null
        defaultTidbitShouldBeFound("url.specified=true");

        // Get all the tidbitList where url is null
        defaultTidbitShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    public void getAllTidbitsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        Category category = CategoryResourceIntTest.createEntity(em);
        em.persist(category);
        em.flush();
        tidbit.setCategory(category);
        tidbitRepository.saveAndFlush(tidbit);
        Long categoryId = category.getId();

        // Get all the tidbitList where category equals to categoryId
        defaultTidbitShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the tidbitList where category equals to categoryId + 1
        defaultTidbitShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTidbitShouldBeFound(String filter) throws Exception {
        restTidbitMockMvc.perform(get("/api/tidbits?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tidbit.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));

        // Check, that the count call also returns 1
        restTidbitMockMvc.perform(get("/api/tidbits/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTidbitShouldNotBeFound(String filter) throws Exception {
        restTidbitMockMvc.perform(get("/api/tidbits?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTidbitMockMvc.perform(get("/api/tidbits/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingTidbit() throws Exception {
        // Get the tidbit
        restTidbitMockMvc.perform(get("/api/tidbits/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTidbit() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        int databaseSizeBeforeUpdate = tidbitRepository.findAll().size();

        // Update the tidbit
        Tidbit updatedTidbit = tidbitRepository.findById(tidbit.getId()).get();
        // Disconnect from session so that the updates on updatedTidbit are not directly saved in db
        em.detach(updatedTidbit);
        updatedTidbit
            .content(UPDATED_CONTENT)
            .author(UPDATED_AUTHOR)
            .source(UPDATED_SOURCE)
            .url(UPDATED_URL);
        TidbitDTO tidbitDTO = tidbitMapper.toDto(updatedTidbit);

        restTidbitMockMvc.perform(put("/api/tidbits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tidbitDTO)))
            .andExpect(status().isOk());

        // Validate the Tidbit in the database
        List<Tidbit> tidbitList = tidbitRepository.findAll();
        assertThat(tidbitList).hasSize(databaseSizeBeforeUpdate);
        Tidbit testTidbit = tidbitList.get(tidbitList.size() - 1);
        assertThat(testTidbit.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTidbit.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testTidbit.getSource()).isEqualTo(UPDATED_SOURCE);
        assertThat(testTidbit.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingTidbit() throws Exception {
        int databaseSizeBeforeUpdate = tidbitRepository.findAll().size();

        // Create the Tidbit
        TidbitDTO tidbitDTO = tidbitMapper.toDto(tidbit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTidbitMockMvc.perform(put("/api/tidbits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tidbitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tidbit in the database
        List<Tidbit> tidbitList = tidbitRepository.findAll();
        assertThat(tidbitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTidbit() throws Exception {
        // Initialize the database
        tidbitRepository.saveAndFlush(tidbit);

        int databaseSizeBeforeDelete = tidbitRepository.findAll().size();

        // Delete the tidbit
        restTidbitMockMvc.perform(delete("/api/tidbits/{id}", tidbit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Tidbit> tidbitList = tidbitRepository.findAll();
        assertThat(tidbitList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tidbit.class);
        Tidbit tidbit1 = new Tidbit();
        tidbit1.setId(1L);
        Tidbit tidbit2 = new Tidbit();
        tidbit2.setId(tidbit1.getId());
        assertThat(tidbit1).isEqualTo(tidbit2);
        tidbit2.setId(2L);
        assertThat(tidbit1).isNotEqualTo(tidbit2);
        tidbit1.setId(null);
        assertThat(tidbit1).isNotEqualTo(tidbit2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TidbitDTO.class);
        TidbitDTO tidbitDTO1 = new TidbitDTO();
        tidbitDTO1.setId(1L);
        TidbitDTO tidbitDTO2 = new TidbitDTO();
        assertThat(tidbitDTO1).isNotEqualTo(tidbitDTO2);
        tidbitDTO2.setId(tidbitDTO1.getId());
        assertThat(tidbitDTO1).isEqualTo(tidbitDTO2);
        tidbitDTO2.setId(2L);
        assertThat(tidbitDTO1).isNotEqualTo(tidbitDTO2);
        tidbitDTO1.setId(null);
        assertThat(tidbitDTO1).isNotEqualTo(tidbitDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tidbitMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tidbitMapper.fromId(null)).isNull();
    }
}
